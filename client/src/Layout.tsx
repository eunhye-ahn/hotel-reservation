import { Outlet } from "react-router";
import './css/header.css'
import { Header } from "@/common/component/header";
import '@/Layout.css'

export default function Layout() {
    return (
        <>
            <Header />
            <main className="main-content">
                <Outlet />
            </main>
        </>
    );
}