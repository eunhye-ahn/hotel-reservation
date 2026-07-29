import bannerImg from '@/asset/banner.jpg'
import styles from '@/common/component/HeroSection.module.css'

interface HeroSectionProps {
    onRegionClick: () => void
}

export const HeroSection = ({ onRegionClick }: HeroSectionProps) => {
    return (
        <div className={styles.hero} style={{ backgroundImage: `url(${bannerImg})` }}>
            <div className={styles.textBlock}>
                <p className={styles.label}>A PLACE TO BELONG</p>
                <h1 className={styles.title}>Stay N</h1>
                <p className={styles.subtitle}>빠른 검색, 안전한 결제, 편안한 숙박</p>
                <span className={styles.regionLink} onClick={onRegionClick}>
                    지역 선택 →
                </span>
            </div>
        </div>
    )
}