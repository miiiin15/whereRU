import org.gradle.api.artifacts.VersionCatalog

fun VersionCatalog.getLibrary(library: String) = findLibrary(library).get()

fun VersionCatalog.getBundle(bundle: String) = findBundle(bundle).get()
