package com.hotel.hotel.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.WishCollection;
import com.hotel.hotel.domain.WishList;
import com.hotel.hotel.dto.AddWishListResponse;
import com.hotel.hotel.dto.WishListCollectionResponse;
import com.hotel.hotel.dto.WishListResponse;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.hotel.repository.WishCollectionRepository;
import com.hotel.hotel.repository.WishListRepository;
import com.hotel.reservation.domain.User;
import com.hotel.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishService {
    private final WishCollectionRepository wishCollectionRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final WishListRepository wishListRepository;

    //컬렉션조회
    public List<WishListCollectionResponse> getCollections(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        //유저의 컬렉션 가져오기
        List<WishCollection> collections = wishCollectionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        //컬렉션이 이미 리스트인데 여기서 내부리스트를 어떻게 또 가져오지????
        //=> 컬렉션 하나하나마다 내부 위시리스트 조회해서 DTO로 변환
        return collections.stream()
                .map(collection -> {
                    List<WishList> wishLists = wishListRepository.findByWishCollectionId(collection.getId());
                    return WishListCollectionResponse.builder()
                            .collectionId(collection.getId())
                            .name(collection.getName())
                            .items(wishLists.stream()
                                    .map(WishListResponse::from)
                                    .toList()
                            )
                            .build();
                })
                .toList();
    }

    //컬렉션 생성
    public WishCollection createCollection(Long userId, String collectionName){
        //이름 중복검사
        if(wishCollectionRepository.existsByUserIdAndName(userId, collectionName)){
            throw new CustomException(ErrorCode.COLLECTION_ALREADY_EXISTS);
        }

        //콜렉션 생성
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        return wishCollectionRepository.save(new WishCollection(user, collectionName));
    }

    //리스트 생성
    public AddWishListResponse addWishList(Long userId, Long hotelId){
        //호텔아이디검사
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(()->new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        WishCollection wishCollection;
        //콜렉션 아이디 기준
        if(!wishCollectionRepository.existsByUserId(userId)){
            wishCollection = createCollection(userId, "기본");
        }
        else{
            wishCollection = wishCollectionRepository.findTop1ByUserIdOrderByCreatedAtDesc(userId)
                    .orElseThrow(()->new CustomException(ErrorCode.COLLECTION_NOT_FOUND));
        }

        WishList wishList = wishListRepository.save(new WishList(wishCollection, hotel));

        return new AddWishListResponse(wishCollection.getName(), wishList.getHotel().getImageUrl());
    }

    //그룹 상세조회
    public WishListCollectionResponse getCollection(Long userId, Long collectionId){
        //그룹 찾기
        WishCollection wishCollection = wishCollectionRepository.findById(collectionId)
                .orElseThrow(()->new CustomException(ErrorCode.COLLECTION_NOT_FOUND));
        WishListCollectionResponse lists = WishListCollectionResponse.from(wishCollection, )
    }

}
