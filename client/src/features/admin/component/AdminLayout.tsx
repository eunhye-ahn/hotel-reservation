import { Outlet } from "react-router"
import { AdminSidebar } from "./AdminSidebar"

export default function AdminLayout() {
    return (
        <div className="flex min-h-[calc(100vh-var(--header-height))]">
            <AdminSidebar />
            <div className="flex-1 p-6 max-w-[1400px]">
                <Outlet />
            </div>
        </div>
    )
}