export type HotelType = {
    code: string,
    name: string
}

export const HOTEL_TYPES: HotelType[] = [
    { code: "AC01", name: "호텔" },
    { code: "AC02", name: "콘도미니엄" },
    { code: "AC03", name: "펜션/민박" },
    { code: "AC04", name: "모텔" },
    { code: "AC05", name: "캠핑" },
    { code: "AC06", name: "호스텔" },
]