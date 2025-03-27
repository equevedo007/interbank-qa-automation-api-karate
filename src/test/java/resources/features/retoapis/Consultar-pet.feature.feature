Feature: Reto Técnico BackEnd

  Background:
    * url urlBase
    * def responseCrear = call read('Create-pet.feature.feature@NuevaMascota01')
    * def idMascota = responseCrear.idMascota


  @ConsultarMascota @Scenario01
  Scenario: Consultar la mascota recién creada
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

