import { useState } from "react"
import { useNavigate } from "react-router";

export const SearchBar = () => {
    const [q, setQ] = useState<string>();
    const navigate = useNavigate();

    const handleSearch =()=>{
        if(!q?.trim()) return;
        navigate(`/hotels/list?q=${q}`)
    }

    return(
        <div>
            <input type="text" value={q}
            onChange={(e)=>setQ(e.target.value)}
            onKeyDown={(e)=> e.key === "Enter" && handleSearch()}
            placeholder="호텔명, 주소 검색"
            />
        </div>
    )
}


