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
        maven {
            url = uri("https://jitpack.io")
        }
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        }

//        maven {
//            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
//            authentication {
//                create<BasicAuthentication>("basic")
//            }
//            credentials {
//                // Do not change the username below.
//                // This should always be `mapbox` (not your username).
//                username = "mapbox"
//                // Use the secret token you stored in gradle.properties as the password
//                password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
//            }
            //create("basic")
            //credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                //username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                //password = project.findProperty("MAPBOX_DOWNLOADS_TOKEN")?.toString() ?: ""
            //}
//        }



        /*maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication.create<BasicAuthentication>("basic")
            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = "pk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsc3ZwcjRiajFuNXQya3FrZnRlanM5dDEifQ.Z2qH4uwufB38l75-YaMCng"
            }
        }*/
    }
}

rootProject.name = "Fatty Food"
include(":app")
