package jp.mangaka.ssp.util.exception

fun filterStackTrace(elements: Array<StackTraceElement>): Array<StackTraceElement> {
    return elements.filter {
        it.className.startsWith("jp.mangaka.ssp")
    }.toTypedArray()
}
