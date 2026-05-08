import { Outlet } from "react-router";
import { Header } from "@/component/header";
import '@/component/header.css'

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