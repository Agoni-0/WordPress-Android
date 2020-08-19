package org.wordpress.android.ui.photopicker.mediapicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import org.wordpress.android.BuildConfig
import org.wordpress.android.R
import org.wordpress.android.WordPress
import org.wordpress.android.fluxc.Dispatcher
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.store.MediaStore
import org.wordpress.android.imageeditor.preview.PreviewImageFragment
import org.wordpress.android.ui.ActivityLauncher
import org.wordpress.android.ui.LocaleAwareActivity
import org.wordpress.android.ui.RequestCodes
import org.wordpress.android.ui.media.MediaBrowserActivity
import org.wordpress.android.ui.media.MediaBrowserType
import org.wordpress.android.ui.media.MediaBrowserType.FEATURED_IMAGE_PICKER
import org.wordpress.android.ui.media.MediaBrowserType.WP_STORIES_MEDIA_PICKER
import org.wordpress.android.ui.photopicker.MediaPickerConstants.EXTRA_LAUNCH_WPSTORIES_CAMERA_REQUESTED
import org.wordpress.android.ui.photopicker.MediaPickerConstants.EXTRA_MEDIA_ID
import org.wordpress.android.ui.photopicker.MediaPickerConstants.EXTRA_MEDIA_QUEUED
import org.wordpress.android.ui.photopicker.MediaPickerConstants.EXTRA_MEDIA_SOURCE
import org.wordpress.android.ui.photopicker.MediaPickerConstants.EXTRA_MEDIA_URIS
import org.wordpress.android.ui.photopicker.MediaPickerConstants.LOCAL_POST_ID
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerActivity.MediaPickerMediaSource.ANDROID_CAMERA
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerActivity.MediaPickerMediaSource.ANDROID_PICKER
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerActivity.MediaPickerMediaSource.APP_PICKER
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerActivity.MediaPickerMediaSource.STOCK_MEDIA_PICKER
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerActivity.MediaPickerMediaSource.WP_MEDIA_PICKER
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.Companion.newInstance
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon.ANDROID_CAPTURE_PHOTO
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon.ANDROID_CAPTURE_VIDEO
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon.ANDROID_CHOOSE_PHOTO
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon.ANDROID_CHOOSE_VIDEO
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon.STOCK_MEDIA
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon.WP_MEDIA
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerIcon.WP_STORIES_CAPTURE
import org.wordpress.android.ui.photopicker.mediapicker.MediaPickerFragment.MediaPickerListener
import org.wordpress.android.ui.posts.EMPTY_LOCAL_POST_ID
import org.wordpress.android.ui.posts.FeaturedImageHelper
import org.wordpress.android.ui.posts.FeaturedImageHelper.EnqueueFeaturedImageResult.FILE_NOT_FOUND
import org.wordpress.android.ui.posts.FeaturedImageHelper.EnqueueFeaturedImageResult.INVALID_POST_ID
import org.wordpress.android.ui.posts.FeaturedImageHelper.EnqueueFeaturedImageResult.SUCCESS
import org.wordpress.android.ui.posts.FeaturedImageHelper.TrackableEvent.IMAGE_PICKED
import org.wordpress.android.ui.posts.editor.ImageEditorTracker
import org.wordpress.android.util.AppLog
import org.wordpress.android.util.AppLog.T.MEDIA
import org.wordpress.android.util.ListUtils
import org.wordpress.android.util.ToastUtils
import org.wordpress.android.util.WPMediaUtils
import java.io.File
import java.util.ArrayList
import javax.inject.Inject

class MediaPickerActivity : LocaleAwareActivity(), MediaPickerListener {
    private var mMediaCapturePath: String? = null
    private lateinit var mBrowserType: MediaBrowserType

    // note that the site isn't required and may be null
    private var mSite: SiteModel? = null

    // note that the local post id isn't required (default value is EMPTY_LOCAL_POST_ID)
    private var mLocalPostId: Int? = null

    @Inject lateinit var mDispatcher: Dispatcher

    @Inject lateinit var mMediaStore: MediaStore

    @Inject lateinit var mFeaturedImageHelper: FeaturedImageHelper

    @Inject lateinit var mImageEditorTracker: ImageEditorTracker

    enum class MediaPickerMediaSource {
        ANDROID_CAMERA, ANDROID_PICKER, APP_PICKER, WP_MEDIA_PICKER, STOCK_MEDIA_PICKER;

        companion object {
            fun fromString(strSource: String?): MediaPickerMediaSource? {
                if (strSource != null) {
                    for (source in values()) {
                        if (source.name.equals(strSource, ignoreCase = true)) {
                            return source
                        }
                    }
                }
                return null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as WordPress).component().inject(this)
        setContentView(R.layout.photo_picker_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
        }
        if (savedInstanceState == null) {
            mBrowserType = intent.getSerializableExtra(MediaBrowserActivity.ARG_BROWSER_TYPE) as MediaBrowserType
            mSite = intent.getSerializableExtra(WordPress.SITE) as SiteModel
            mLocalPostId = intent.getIntExtra(LOCAL_POST_ID, EMPTY_LOCAL_POST_ID)
        } else {
            mBrowserType = savedInstanceState.getSerializable(MediaBrowserActivity.ARG_BROWSER_TYPE) as MediaBrowserType
            mSite = savedInstanceState.getSerializable(WordPress.SITE) as SiteModel
            mLocalPostId = savedInstanceState.getInt(LOCAL_POST_ID, EMPTY_LOCAL_POST_ID)
        }
        var fragment = pickerFragment
        if (fragment == null) {
            fragment = newInstance(this, mBrowserType!!, mSite)
            supportFragmentManager.beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            fragment,
                            PICKER_FRAGMENT_TAG
                    )
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commitAllowingStateLoss()
        } else {
            fragment.setMediaPickerListener(this)
        }
        updateTitle(mBrowserType, actionBar)
    }

    private fun updateTitle(browserType: MediaBrowserType?, actionBar: ActionBar?) {
        if (browserType!!.isImagePicker && browserType.isVideoPicker) {
            actionBar!!.setTitle(R.string.photo_picker_photo_or_video_title)
        } else if (browserType.isVideoPicker) {
            actionBar!!.setTitle(R.string.photo_picker_video_title)
        } else {
            actionBar!!.setTitle(R.string.photo_picker_title)
        }
    }

    private val pickerFragment: MediaPickerFragment?
        private get() {
            val fragment = supportFragmentManager.findFragmentByTag(
                    PICKER_FRAGMENT_TAG
            )
            return if (fragment != null) {
                fragment as MediaPickerFragment?
            } else null
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(MediaBrowserActivity.ARG_BROWSER_TYPE, mBrowserType)
        outState.putInt(LOCAL_POST_ID, mLocalPostId!!)
        if (mSite != null) {
            outState.putSerializable(WordPress.SITE, mSite)
        }
        if (!TextUtils.isEmpty(mMediaCapturePath)) {
            outState.putString(KEY_MEDIA_CAPTURE_PATH, mMediaCapturePath)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mMediaCapturePath = savedInstanceState.getString(KEY_MEDIA_CAPTURE_PATH)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            RequestCodes.PICTURE_LIBRARY, RequestCodes.VIDEO_LIBRARY -> if (data != null) {
                doMediaUrisSelected(WPMediaUtils.retrieveMediaUris(data), ANDROID_PICKER)
            }
            RequestCodes.TAKE_PHOTO -> try {
                WPMediaUtils.scanMediaFile(this, mMediaCapturePath!!)
                val f = File(mMediaCapturePath)
                val capturedImageUri = listOf(
                        Uri.fromFile(
                                f
                        )
                )
                doMediaUrisSelected(capturedImageUri, ANDROID_CAMERA)
            } catch (e: RuntimeException) {
                AppLog.e(MEDIA, e)
            }
            RequestCodes.MULTI_SELECT_MEDIA_PICKER, RequestCodes.SINGLE_SELECT_MEDIA_PICKER -> if (data!!.hasExtra(
                            MediaBrowserActivity.RESULT_IDS
                    )) {
                val ids = ListUtils.fromLongArray(
                        data.getLongArrayExtra(
                                MediaBrowserActivity.RESULT_IDS
                        )
                )
                doMediaIdsSelected(ids, WP_MEDIA_PICKER)
            }
            RequestCodes.STOCK_MEDIA_PICKER_SINGLE_SELECT -> if (data != null && data.hasExtra(EXTRA_MEDIA_ID)) {
                val mediaId = data.getLongExtra(EXTRA_MEDIA_ID, 0)
                val ids = ArrayList<Long>()
                ids.add(mediaId)
                doMediaIdsSelected(ids, STOCK_MEDIA_PICKER)
            }
            RequestCodes.IMAGE_EDITOR_EDIT_IMAGE -> if (data != null && data.hasExtra(PreviewImageFragment.ARG_EDIT_IMAGE_DATA)) {
                val uris = WPMediaUtils.retrieveImageEditorResult(data)
                doMediaUrisSelected(uris, APP_PICKER)
            }
        }
    }

    private fun launchCameraForImage() {
        WPMediaUtils.launchCamera(
                this, BuildConfig.APPLICATION_ID
        ) { mediaCapturePath: String? -> mMediaCapturePath = mediaCapturePath }
    }

    private fun launchCameraForVideo() {
        WPMediaUtils.launchVideoCamera(this)
    }

    private fun launchPictureLibrary(multiSelect: Boolean) {
        WPMediaUtils.launchPictureLibrary(this, multiSelect)
    }

    private fun launchVideoLibrary(multiSelect: Boolean) {
        WPMediaUtils.launchVideoLibrary(this, multiSelect)
    }

    private fun launchWPMediaLibrary() {
        if (mSite != null) {
            ActivityLauncher.viewMediaPickerForResult(this, mSite!!, mBrowserType!!)
        } else {
            ToastUtils.showToast(this, R.string.blog_not_found)
        }
    }

    private fun launchStockMediaPicker() {
        if (mSite != null) {
            ActivityLauncher.showStockMediaPickerForResult(
                    this,
                    mSite!!, RequestCodes.STOCK_MEDIA_PICKER_SINGLE_SELECT
            )
        } else {
            ToastUtils.showToast(this, R.string.blog_not_found)
        }
    }

    private fun launchWPStoriesCamera() {
        val intent = Intent()
                .putExtra(EXTRA_LAUNCH_WPSTORIES_CAMERA_REQUESTED, true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun doMediaUrisSelected(
        mediaUris: List<Uri>,
        source: MediaPickerMediaSource
    ) {
        // if user chose a featured image, we need to upload it and return the uploaded media object
        if (mBrowserType == FEATURED_IMAGE_PICKER) {
            val mediaUri = mediaUris[0]
            val mimeType = contentResolver.getType(mediaUri)
            mFeaturedImageHelper!!.trackFeaturedImageEvent(
                    IMAGE_PICKED,
                    mLocalPostId!!
            )
            WPMediaUtils.fetchMediaAndDoNext(
                    this, mediaUri
            ) { uri ->
                val queueImageResult = mFeaturedImageHelper
                        .queueFeaturedImageForUpload(
                                mLocalPostId!!, mSite!!, uri,
                                mimeType
                        )
                when (queueImageResult) {
                    FILE_NOT_FOUND -> Toast.makeText(
                            applicationContext,
                            R.string.file_not_found, Toast.LENGTH_SHORT
                    )
                            .show()
                    INVALID_POST_ID -> Toast.makeText(
                            applicationContext,
                            R.string.error_generic, Toast.LENGTH_SHORT
                    )
                            .show()
                    SUCCESS -> {
                    }
                }
                val intent = Intent()
                        .putExtra(EXTRA_MEDIA_QUEUED, true)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        } else {
            val intent = Intent()
                    .putExtra(EXTRA_MEDIA_URIS, convertUrisListToStringArray(mediaUris))
                    .putExtra(
                            EXTRA_MEDIA_SOURCE,
                            source.name
                    ) // set the browserType in the result, so caller can distinguish and handle things as needed
                    .putExtra(MediaBrowserActivity.ARG_BROWSER_TYPE, mBrowserType)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun doMediaIdsSelected(
        mediaIds: ArrayList<Long>?,
        source: MediaPickerMediaSource
    ) {
        if (mediaIds != null && mediaIds.size > 0) {
            if (mBrowserType == WP_STORIES_MEDIA_PICKER) {
                // TODO WPSTORIES add TRACKS (see how it's tracked below? maybe do along the same lines)
                val data = Intent()
                        .putExtra(
                                MediaBrowserActivity.RESULT_IDS,
                                ListUtils.toLongArray(mediaIds)
                        )
                        .putExtra(MediaBrowserActivity.ARG_BROWSER_TYPE, mBrowserType)
                        .putExtra(EXTRA_MEDIA_SOURCE, source.name)
                setResult(Activity.RESULT_OK, data)
                finish()
            } else {
                // if user chose a featured image, track image picked event
                if (mBrowserType == FEATURED_IMAGE_PICKER) {
                    mFeaturedImageHelper!!.trackFeaturedImageEvent(
                            IMAGE_PICKED,
                            mLocalPostId!!
                    )
                }
                val data = Intent()
                        .putExtra(EXTRA_MEDIA_ID, mediaIds[0])
                        .putExtra(EXTRA_MEDIA_SOURCE, source.name)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        } else {
            throw IllegalArgumentException("call to doMediaIdsSelected with null or empty mediaIds array")
        }
    }

    override fun onMediaChosen(uriList: List<Uri>) {
        if (uriList.size > 0) {
            doMediaUrisSelected(uriList, APP_PICKER)
        }
    }

    override fun onIconClicked(icon: MediaPickerIcon, multiple: Boolean) {
        when (icon) {
            ANDROID_CAPTURE_PHOTO -> launchCameraForImage()
            ANDROID_CHOOSE_PHOTO -> launchPictureLibrary(multiple)
            ANDROID_CAPTURE_VIDEO -> launchCameraForVideo()
            ANDROID_CHOOSE_VIDEO -> launchVideoLibrary(multiple)
            WP_MEDIA -> launchWPMediaLibrary()
            STOCK_MEDIA -> launchStockMediaPicker()
            WP_STORIES_CAPTURE -> launchWPStoriesCamera()
        }
    }

    private fun convertUrisListToStringArray(uris: List<Uri>): Array<String?> {
        val stringUris = arrayOfNulls<String>(uris.size)
        for (i in uris.indices) {
            stringUris[i] = uris[i].toString()
        }
        return stringUris
    }

    companion object {
        private const val PICKER_FRAGMENT_TAG = "picker_fragment_tag"
        private const val KEY_MEDIA_CAPTURE_PATH = "media_capture_path"
    }
}
