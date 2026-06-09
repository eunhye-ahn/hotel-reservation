import { getSearchAutocomplete } from "@/api/reservation-service";
import { Search } from "lucide-react";
import { useRef, useState } from "react"
import { useNavigate } from "react-router";

export const SearchBar = () => {
    const [q, setQ] = useState<string>();
    const navigate = useNavigate();
    const [suggestions, setSuggestions] = useState<string[]>([])
    const timerRef = useRef<ReturnType<typeof setTimeout>>(0)
    const [open, setOpen] = useState<boolean>(false)


    const handleSearch =(keyword?: string)=>{
        const target = keyword ?? q
        if(!target?.trim()) return;
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


/**
 * 
 * import { useRef, useState } from "react"
import { SearchAutocomplete } from "../../axios/api";

type SearchBarProps = {
    onSearch: (q: string) => void
}

export const SearchBar = ({ onSearch }: SearchBarProps) => {
    const [q, setQ] = useState("");
    const [suggestions, setSuggestions] = useState<string[]>([])
    const timerRef = useRef<ReturnType<typeof setTimeout>>(0)

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value
        setQ(value)

        clearTimeout(timerRef.current)

        timerRef.current = setTimeout(() => {
            if (value) {
                SearchAutocomplete(value)
                    .then((res) => setSuggestions(res.data))
            }
            else {
                setSuggestions([]);
            }
        }, 300)
    }

    return (
        <div>
            <input type="text"
                value={q}
                onChange={handleChange} />
            <button onClick={() => {
                onSearch(q)
                setSuggestions([]);
            }}>검색</button>
            {suggestions.length > 0 && (
                <div>
                    {suggestions.map(s => (
                        <div key={s}
                            onClick={() => {
                                onSearch(s)
                                setSuggestions([]);
                            }}>
                            {s}
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
 */