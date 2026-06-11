import { getSearchAutocomplete } from "@/api/reservation-service";
import { Search } from "lucide-react";
import { useEffect, useRef, useState } from "react"
import { useLocation, useNavigate } from "react-router";

export const SearchBar = () => {
    const [q, setQ] = useState<string>();
    const navigate = useNavigate();
    const [suggestions, setSuggestions] = useState<string[]>([])
    const timerRef = useRef<ReturnType<typeof setTimeout>>(0)
    const [open, setOpen] = useState<boolean>(false)
    const location = useLocation()

    useEffect(() => {
        if (!location.pathname.startsWith("/hotels")) {
        setQ("")
        setSuggestions([])
        setOpen(false)
        }
    }, [location.pathname])

    const handleSearch =(keyword?: string)=>{
        const target = keyword ?? q
        if(!target?.trim()) return;
        setOpen(false)
        setSuggestions([])
        navigate(`/hotels/list?q=${target}`)
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value
        setQ(value)
        //키 입력마다 이전 타이머 취소하고 새로 등록
        clearTimeout(timerRef.current)

        timerRef.current = setTimeout(()=>{
            //마지막 입력 후 200ms뒤에만 실행
            if(value.trim()){
                getSearchAutocomplete(value)
                .then((res)=>{
                    console.log(res.data)
                    setSuggestions(res.data)
                    setOpen(true)
                })
                .catch(()=>setSuggestions([]))
            }else{
                setSuggestions([])
                setOpen(false)
            }
        },200)
        
    }

    return(
        <div className="search-bar-wrapper">
            <div className="search-bar">
                <Search size={18} className="search-icon"/>
                <input type="text" value={q}
                onChange={handleChange}
                onKeyDown={(e)=> {
                    if(e.key === "Enter"){
                        handleSearch()
                        setOpen(false)
                    }
                }}
                placeholder="호텔명, 주소 검색"
                className="search-input"
                />
                {open && (
                <div className="search-dropdown">
                    {suggestions.map((s)=>(
                        <li key={s}
                            className="search-dropdown-item"
                            onClick={()=>{
                                handleSearch(s)
                                setOpen(false)
                            }}
                        >
                        {s}
                        </li>
                    ))}
                </div>
            )}
            </div> 
        </div>
    )
}