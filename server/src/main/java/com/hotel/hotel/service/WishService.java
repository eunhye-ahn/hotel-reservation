package com.hotel.hotel.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.WishCollection;
import com.hotel.hotel.domain.WishList;
import com.hotel.hotel.dto.*;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.hotel.repository.WishCollectionRepository;
import com.hotel.hotel.repository.WishListRepository;
import com.hotel.user.domain.User;
import com.hotel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return collections.stream()
                .map(collection -> {
                    List<WishList> wishLists = wishListRepository.findByWishCollectionId(collection.getId());
                    return WishListCollectionResponse.from(collection, wishLists);
                })
                .toList();
    }

    //컬렉션 생성
    @Transactional
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
    @Transactional
    public AddWishListResponse addWishList(Long userId, Long hotelId, Long collectionId){
        //호텔아이디검사
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(()->new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        WishCollection wishCollection;

        if (collectionId == null) {
            throw new CustomException(ErrorCode.COLLECTION_SELECT_REQUIRED);
        }

        Optional<WishCollection> myCollection = wishCollectionRepository.findByIdAndUserId(collectionId, userId);

        if(myCollection.isPresent()){
            wishCollection = myCollection.get();
        } else{
            if(!wishCollectionRepository.existsByUserId(userId)){
                wishCollection = createCollection(userId, "기본");
            }
            else {
                throw new CustomException(ErrorCode.COLLECTION_SELECT_REQUIRED);
            }
        }

        WishList wishList = wishListRepository.save(new WishList(wishCollection, hotel));

        return AddWishListResponse.from(wishCollection, wishList);
    }

    //그룹 상세조회
    public WishListCollectionResponse getCollection(Long userId, Long collectionId){
        //그룹 찾기
        WishCollection wishCollection = wishCollectionRepository.findById(collectionId)
                .orElseThrow(()->new CustomException(ErrorCode.COLLECTION_NOT_FOUND));
        //리스트 찾기
        List<WishList> items = wishListRepository.findByWishCollectionId(collectionId);
        return WishListCollectionResponse.from(wishCollection,items);
    }

    //리스트 그룹 이동
    @Transactional
    public MoveWishResponse moveCollection(MoveWishRequest request){
        WishList list = wishListRepository.findById(request.getListId())
                .orElseThrow(()->new CustomException(ErrorCode.WISHLIST_NOT_FOUND));
        WishCollection collection = wishCollectionRepository.findById(request.getCollectionId())
                .orElseThrow(()->new CustomException(ErrorCode.COLLECTION_NOT_FOUND));

        list.updateCollection(collection);

        return new MoveWishResponse(collection.getName(), list.getHotel().getImageUrl());
    }

    //위시리스트 취소
    @Transactional
    public void cancelWishList(Long userId, Long hotelId){
        List<WishCollection> collections = wishCollectionRepository.findByUserId(userId);

        for(WishCollection collection : collections){
            wishListRepository.findByWishCollectionIdAndHotelId(collection.getId(),hotelId)
                    .ifPresent(list-> wishListRepository.delete(list));
        }
    }

    //콜렉션 삭제
    @Transactional
    public void deleteCollection(Long collectionId){
        WishCollection collection = wishCollectionRepository.findById(collectionId)
                        .orElseThrow(()->new CustomException(ErrorCode.COLLECTION_NOT_FOUND));
        wishListRepository.deleteByWishCollection(collection);
        wishCollectionRepository.deleteById(collectionId);
    }

    //위시상태 조회
    public boolean getWishedStatus(Long userId, Long hotelId){

        List<WishCollection> collections= wishCollectionRepository.findByUserId(userId);

        boolean wCheck = false;
        for(WishCollection collection : collections) {
            wCheck = wishListRepository.existsByWishCollectionIdAndHotelId(collection.getId(), hotelId);
            if(wCheck){
                break;
            }
        }
        return wCheck;
    }
}
