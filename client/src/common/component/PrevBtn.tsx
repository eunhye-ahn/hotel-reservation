import { useNavigate } from "react-router"

export const PrevBtn = () => {
    const navigate = useNavigate()

    return (
        <div className="text-left mt-5">
            <button className="px-3 py-1 rounded-lg bg-gray-200"
                onClick={() => navigate(-1)}
            >
                &laquo;
            </button>
        </div>
    )
}