#!/bin/bash -eu

echo "--- :rubygems: Setting up Gems"
install_gems

echo "--- :clipboard: Copying gradle.properties"
cp gradle.properties-example gradle.properties

echo "--- :globe_with_meridians: Check Locales Declaration Consistency"
bundle exec fastlane check_declared_locales_consistency app:"$1"

echo "--- :microscope: Linting"

if [ "$1" = "wordpress" ]; then
  set +e
	./gradlew lintWordpressVanillaRelease
	lint_exit_code=$?
	set -e

	upload_sarif_to_github "WordPress/build/reports/lint-results-wordpressVanillaRelease.sarif"
	exit $lint_exit_code
fi

if [ "$1" = "jetpack" ]; then
	./gradlew lintJetpackVanillaRelease
	exit 0
fi

echo "No target provided – unable to lint"
exit 1
