import { getHotels } from "@/axios/api";
import {type hotelResponse, type Page } from "@/type/hotel";
import { useEffect, useState } from "react"
import '@/pages/MainPage.css';
import { useNavigate } from "react-router";

//호텔정보페이지
export const MainPage = () => {
    const [data,setData] = useState<Page<hotelResponse>>();
    const navigate = useNavigate();

    useEffect(()=>{
        getHotels()
        .then((res)=>setData(res.data))
        .catch((err)=>alert(err.message))
    },[]);

    return (
        <div className="hotel-list">
            {data?.content.map((hotel) => (
                <div 
                key={hotel.hotelId} className="hotel-card"
                onClick={() => navigate(`/hotels/${hotel.hotelId}`)} >
                    <img className="hotel-img" src={hotel.imageUrl} />
                    <p className="hotel-name">{hotel.name}</p>
                    <p className="hotel-address">{hotel.address}</p>
                    <p className="hotel-checkin">숙박 {hotel.checkInTime.substring(0,5)}~</p>
                    <div className="hotel-price-row">
                        <span className="hotel-original">{hotel.maxRate.toLocaleString()}</span>
                        <span className="hotel-discount">{hotel.discountRate}%</span>
                    </div>
                    <p className="hotel-demand">{hotel.demandRate.toLocaleString()}원</p>
                </div>
            ))}
        </div>
    )
}