package com.arematics.minecraft.optimizer.clearlag;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ClearLagService {

    private final List<ClearLagTask> clearLagTaskList;


}
