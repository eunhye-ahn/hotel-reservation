import { useState } from "react"

export function CartPage(){
    const [selectedIds, setSelectedIds] = useState<string[]>([]);


    return(
        <div>
            <div>
                <input type="checkbox" />
                <p>전체선택 </p>
                <button>선택 삭제</button>
            </div>
            <div>
                <hr/>
                <input type="checkbox" />
            </div>
        </div>
    )
}