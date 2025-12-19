package com.shoutx.services;

import com.shoutx.models.Plan;
import com.shoutx.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public List<Plan> getAllPlans() {
        return planRepository.findAllByOrderByPriceAsc();
    }

    public Optional<Plan> getPlanById(String planId) {
        return planRepository.findById(planId);
    }

    public Optional<Plan> getPlanByName(String name) {
        return planRepository.findByName(name);
    }

    public Plan createPlan(Plan plan) {
        return planRepository.save(plan);
    }
}
