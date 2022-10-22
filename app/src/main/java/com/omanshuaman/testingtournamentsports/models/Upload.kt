package com.omanshuaman.testingtournamentsports.models

class Upload {
    var imageUrl: String? = null
    var matchDate: String? = null
    var imageUrlLow: String? = null
    var requirement: String? = null
    var sports: String? = null
    var address: String? = null
    var tournamentName: String? = null
    var longitude: String? = null
    var latitude: String? = null
    var Id: String? = null
    var SportsType: String? = null
    var entryFee: String? = null
    var prizeMoney: String? = null
    var uid: String? = null
    var imageUrlMid: String? = null


    constructor(
        Id: String?,
        tournamentName: String?,
        matchDate: String?,
        imageUrlLow: String?,
        requirement: String?,
        sports: String?,
        address: String?,
        imageUrl: String?,
        longitude: String?,
        latitude: String?,
        SportsType: String?,
        entryFee: String?,
        prizeMoney: String?,
        uid: String?,
        imageUrlMid: String?

    ) {
        this.Id = Id
        this.imageUrl = imageUrl
        this.tournamentName = tournamentName
        this.matchDate = matchDate
        this.imageUrlLow = imageUrlLow
        this.requirement = requirement
        this.sports = sports
        this.address = address
        this.longitude = longitude
        this.latitude = latitude
        this.SportsType = SportsType
        this.entryFee = entryFee
        this.prizeMoney = prizeMoney
        this.uid = uid
        this.imageUrlMid = imageUrlMid


    }

    constructor() {}
}
