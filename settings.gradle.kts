rootProject.name = "spout-dungeons"

// Local dev: substitute with the spout-api source project directly
if (File("../Spout/spout-paper").exists()) {
    includeBuild("../Spout/spout-paper") {
        dependencySubstitution {
            substitute(module("com.github.zoumath19.Spout:spout-api")).using(project(":spout-api"))
        }
    }
}
