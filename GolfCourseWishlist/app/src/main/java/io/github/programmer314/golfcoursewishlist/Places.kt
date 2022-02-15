package io.github.programmer314.golfcoursewishlist

class Places {
    companion object {
        var placeNameArray = arrayOf(
            "Black Mountain",
            "Chambers Bay",
            "Clear Water",
            "Harbour Town",
            "Muirfield",
            "Old Course",
            "Pebble Beach",
            "Spy Class"
        )

        fun placeList(): ArrayList<Place> {
            val list = ArrayList<Place>()
            for (i in placeNameArray.indices) {
                list.add(
                    Place(
                        placeNameArray[i],
                        placeNameArray[i].replace("\\s+".toRegex(), "").lowercase()
                    )
                )
            }

            return list
        }
    }
}