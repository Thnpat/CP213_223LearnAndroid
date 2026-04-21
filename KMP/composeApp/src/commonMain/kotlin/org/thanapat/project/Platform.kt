package org.thanapat.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform