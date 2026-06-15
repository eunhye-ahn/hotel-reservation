declare const kakao: any;

import { useEffect, useRef } from "react";

interface MapProps {
    hotelName: string,
    hotelAddress: string
}

export const Map = ({hotelName, hotelAddress}: MapProps) => {
    // 지도를 표시할 div 
    const mapRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!mapRef.current) return;    //DOM이 없으면 종료
        
        const mapOption = {
            center: new kakao.maps.LatLng(33.450701, 126.570667),   // 지도의 중심좌표(기본)
            level: 3,
        };

        //지도 생성
        const map = new kakao.maps.Map(mapRef.current, mapOption);
        // 주소-좌표 변환 객체를 생성
        const geocoder = new kakao.maps.services.Geocoder();

        // props로 받은 주소로 좌표 검색
        geocoder.addressSearch(hotelAddress, function (result: any, status: any) {
            //정상적으로 검색 완료
            if (status === kakao.maps.services.Status.OK) {
                const coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                //결과값으로 받은 위치 마커로 표시
                const marker = new kakao.maps.Marker({
                    map,
                    position: coords,
                });

                // 인포윈도우로 props로 받은 호텔명 표시
                const infowindow = new kakao.maps.InfoWindow({
                    content: `<div style="width:150px;text-align:center;padding:6px 0;">${hotelName}</div>`,
                });
                infowindow.open(map, marker);
                //지도의 중심을 결과값으로 받은 위치로 이동
                map.setCenter(coords);
            }
        });
    }, [hotelAddress, hotelName]); // 주소/이름 바뀌면 재실행

    return (
        <div ref={mapRef} style={{ width: "100%", height: "400px" }} />
    );
}