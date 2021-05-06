package com.example.transporargo.utils

import com.example.transporargo.main_fragments.ui.Request

object Data {
    const val myId = 345
    val requestsList = mutableListOf(
        Request(
            1.toString(), "342",3f, 4f, "Paris", "48.864716|2.349014","Rome",
            "41.902782|12.496366", Request.TYPE_TRANSPORT, "tilt", "sss", "05.08.20", "+380(56)5810023"
        ),
        Request(
            2.toString(), "342",3f, 4f, "Paris", "48.864716|2.349014","Rome",
            "41.902782|12.496366", Request.TYPE_TRANSPORT, "", "sss", "05.08.20", "+380(34)3533543"
        ),
        Request(
            3.toString(), "342",3f, 4f, "Paris", "48.864716|2.349014","Rome",
            "41.902782|12.496366", Request.TYPE_CARGO, "tilt", "sss", "05.08.20", "+380(56)353534"
        ),
        Request(
            4.toString(), "342",3f, 4f, "Paris", "48.864716|2.349014","Rome",
            "41.902782|12.496366", Request.TYPE_CARGO, "tilt", "sss", "05.08.20", "+380(56)536443"
        ),
        Request(
            5.toString(), "342",3f, 4f, "Kyiv", "50.450001|30.523333","Lviv",
            "49.842957|24.031111", Request.TYPE_CARGO, "sss", "", "05.08.20", "+380(63)5810023"
        )
    )


}