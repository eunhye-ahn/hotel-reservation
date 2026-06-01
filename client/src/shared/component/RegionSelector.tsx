import { REGIONS, type Region, type SubRegion } from "@/constants/Region";
import { useState } from "react";
import '@/shared/component/RegionSelector.css'

interface RegionSelectorProps {
    onSelect: (region: Region, subRegion?: SubRegion) => void;
}

export const RegionSelector = ({onSelect} : RegionSelectorProps) => {
    const [selectedArea, setSelectedArea] = useState<Region>(REGIONS[0]);

    return(
        <div className="region-selector">
            <ul className="area-list">
                {REGIONS.map((area)=>(
                    <li key={area.code} onClick={()=> setSelectedArea(area)}
                     className={`area-item ${selectedArea.code === area.code ? "area-item--active" : ""}`}>
                        {area.name}
                    </li>
                ))}

            </ul>
            <ul  className="sub-region-list">
                <li onClick={()=> onSelect(selectedArea)}
                    className="sub-region-item sub-region-item--all" >
                    {selectedArea.name} 전체
                </li>
                {selectedArea.subRegions.map((sub)=>(
                    <li key={sub.code} onClick={()=>onSelect(selectedArea, sub)}
                     className="sub-region-item">
                        {sub.name}
                    </li>
                ))}
            </ul>
        </div>
    );
}