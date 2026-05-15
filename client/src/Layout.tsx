import { Outlet } from "react-router";
import './shared/component/header.css'
import { Header } from "./shared/component/header";

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