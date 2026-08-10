import { Outlet } from "react-router";
import { Header } from "@/layout/Header";
import { Footer } from "@/layout/Footer";

export default function Layout() {
    return (
        <>
            <Header />
            <main>
                <Outlet />
            </main>
            <Footer />
        </>
    );
}