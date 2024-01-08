package org.wordpress.android.ui.uploads

import android.content.Context
import org.wordpress.android.fluxc.model.MediaModel
import org.wordpress.android.fluxc.model.PostImmutableModel
import org.wordpress.android.fluxc.model.PostModel
import org.wordpress.android.fluxc.model.SiteModel
import javax.inject.Inject

/**
 * An injectable class built on top of [UploadService].
 *
 * The main purpose of this is to provide testability for classes that use [UploadService]. This should never
 * contain any static methods.
 */
class UploadServiceFacade @Inject constructor(private val appContext: Context) {
    fun uploadPost(context: Context, post: PostModel, trackAnalytics: Boolean, sourceForLogging: String = "") {
        val intent = UploadService.getRetryUploadServiceIntent(context, post, trackAnalytics, sourceForLogging)
        context.startService(intent)
    }

    fun uploadPost(context: Context, postId: Int, isFirstTimePublish: Boolean, sourceForLogging: String = "") =
        UploadService.uploadPost(context, postId, isFirstTimePublish, sourceForLogging)

    fun isPostUploadingOrQueued(post: PostImmutableModel) =
        UploadService.isPostUploadingOrQueued(post)

    fun cancelFinalNotification(post: PostImmutableModel) =
        UploadService.cancelFinalNotification(appContext, post)

    fun cancelFinalNotificationForMedia(site: SiteModel) =
        UploadService.cancelFinalNotificationForMedia(appContext, site)

    fun uploadMedia(mediaList: ArrayList<MediaModel>, sourceForLogging: String = "") =
        UploadService.uploadMedia(appContext, mediaList, sourceForLogging)

    fun getPendingOrInProgressFeaturedImageUploadForPost(post: PostImmutableModel): MediaModel? =
        UploadService.getPendingOrInProgressFeaturedImageUploadForPost(post)

    fun uploadMediaFromEditor(mediaList: ArrayList<MediaModel>) {
        UploadService.uploadMediaFromEditor(appContext, mediaList)
    }

    fun isPendingOrInProgressMediaUpload(mediaModel: MediaModel): Boolean =
        UploadService.isPendingOrInProgressMediaUpload(mediaModel)

    fun getUploadProgressForMedia(mediaModel: MediaModel): Float =
        UploadService.getUploadProgressForMedia(mediaModel)
}
