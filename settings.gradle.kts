pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CleanGalleryDeck"

include(":app")
include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:analytics")
include(":core:notification")
include(":feature:deck")
include(":feature:settings")
include(":feature:player")
include(":media-sources:media-source-api")
include(":media-sources:source-local-images")
include(":media-sources:source-local-videos")
include(":media-sources:source-google-photos")
