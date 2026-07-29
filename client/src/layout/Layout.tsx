import { Outlet } from "react-router";
import { Header } from "@/layout/Header";
import { HeroSection } from "../common/component/HeroSection";

export default function Layout() {
    return (
        <>
            <Header />
            <main>
                <Outlet />
            </main>
        </>
    );
}