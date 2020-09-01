package com.arematics.minecraft.core.data.service;

import com.arematics.minecraft.core.data.model.TestModel;
import com.arematics.minecraft.core.data.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestService {

    private final TestRepository repository;

    @Autowired
    public TestService(TestRepository repository){
        this.repository = repository;
    }

    public TestModel getModel(int id){
        Optional<TestModel> model = this.repository.findById(id);
        if(!model.isPresent()) throw new RuntimeException("Model is not existing");
        return model.get();
    }
}
