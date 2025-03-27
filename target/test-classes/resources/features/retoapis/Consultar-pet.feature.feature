Feature: Reto Técnico BackEnd

  Background:
    * url urlBase
    * def ResponseAgregarNuevaMascota = call read('Create-pet.feature.feature@AgregarNuevaMascota')


  @ConsultarMascota @Scenario01
  Scenario: Consultar una mascota inexistente (status 404)
    * def petIdInexistente = 999999999
    Given path 'pet', petIdInexistente
    When method get
    Then status 404: Consultar la mascota agregada usando GET
    * def idMascota = ResponseAgregarNuevaMascota.idMascota
    Given path 'pet', idMascota
    When method get
    Then status 200
    And match response.id == idMascota
    And match response.name == 'Firulais'
    And match response.status == 'available'

  @ConsultarMascota @Scenario02
  Scenario: Consultar la mascota Invalid ID supplied
    Given path 'pet', '651'
    When method get
    Then status 404
    And match response.code == 1
    And match response.type == 'error'
    And match response.message == 'Pet not found'

