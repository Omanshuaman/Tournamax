package com.omanshuaman.testingtournamentsports.models

class LocationModel {

    var longitude: String? = null
    var latitude: String? = null
    constructor( longitude: String?, latitude: String?) {

        this.longitude = longitude
        this.latitude = latitude
    }

    constructor() {}
}
