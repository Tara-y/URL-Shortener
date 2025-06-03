package com.samples.urlshortener.model.entity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import java.time.LocalDateTime

@Entity
@Table(name = "urls")
data class Url(
    @Id
    @Column(name = "short_url", nullable = false, unique = true)
    val shortUrl : String = "",

    @Column(name = "original_url", nullable = false)
    val originalUrl: String = "",

    //in case if we need to have expiration for links
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

//   in case if we want to know which user created this url
//    @Column(nullable=false)
//    val User user;
)