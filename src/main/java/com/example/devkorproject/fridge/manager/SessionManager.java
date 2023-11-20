package com.example.devkorproject.fridge.manager;

import com.example.devkorproject.fridge.entity.FridgeEntity;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {
    private static final Map<Long, Comparator<FridgeEntity>> userSortOrder = new HashMap<>();

    public static void setUserSortOrder(Long userId, Comparator<FridgeEntity> comparator) {
        userSortOrder.put(userId, comparator);
    }

    public static Comparator<FridgeEntity> getUserSortOrder(Long userId) {
        return userSortOrder.get(userId);
    }
}
