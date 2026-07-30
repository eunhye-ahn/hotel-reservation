import { Link } from "react-router"

const menus = [
    { path: '/admin', label: '대시보드' },
    { path: '/admin/reservations', label: '예약 관리' },
    { path: '/admin/payments', label: '결제 관리' },
    { path: '/admin/settlements', label: '정산 관리' },
    { path: '/admin/inventory', label: '재고 관리' },
]

export const AdminSidebar = () => {

    return (
        <aside className="w-[220px] border-r border-gray-200 text-center py-10">
            <p className="font-bold text-lg mb-6">관리자</p>
            <nav className="flex flex-col gap-1">
                {menus.map(menu => (
                    <Link key={menu.path}
                        to={menu.path}
                    >
                        {menu.label}
                    </Link>
                ))}
            </nav>
        </aside>
    )
}