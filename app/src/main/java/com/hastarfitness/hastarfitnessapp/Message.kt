package com.hastarfitness.hastarfitnessapp

internal class Message {
    var author: String? = null
        private set
    var text: String? = null
        private set

    private constructor() {}
    constructor(author: String?, text: String?) {
        this.author = author
        this.text = text
    }

}