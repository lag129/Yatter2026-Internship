package com.dmm.bootcamp.yatter.domain.service

import com.dmm.bootcamp.yatter.domain.model.Relationship
import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.domain.model.Username

interface CheckRelationshipService {
  suspend fun execute(user: User, usernameList: List<Username>): List<Relationship>
}
