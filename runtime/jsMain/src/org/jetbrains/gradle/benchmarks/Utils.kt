package org.jetbrains.gradle.benchmarks

actual fun Double.format(precision: Int): String {
    val options = js("{maximumFractionDigits:2, minimumFractionDigits:2, useGrouping:true}")
    options.minimumFractionDigits = precision
    options.maximumFractionDigits = precision
    return this.asDynamic().toLocaleString(undefined, options)
}

private val fs = org.jetbrains.gradle.benchmarks.js.require("fs")

actual fun saveReport(reportFile: String?, results: Collection<ReportBenchmarkResult>) {
    if (reportFile == null)
        return

    fs.writeFile(reportFile, formatJson(results)) { err -> if (err != null) throw err }
}