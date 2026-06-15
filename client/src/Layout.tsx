import { Outlet } from "react-router";
import './shared/component/header.css'
import { Header } from "./shared/component/header";
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