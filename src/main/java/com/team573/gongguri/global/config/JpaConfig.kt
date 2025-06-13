package com.team573.gongguri.global.config

import com.team573.gongguri.global.annotation.ExcludeFromJpaRepository
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(
    basePackages = ["com.team573.gongguri.domain"],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = arrayOf(ExcludeFromJpaRepository::class))]
)
@EnableJpaAuditing
@Configuration
class JpaConfig 
