package io.github.jokoframework.myproject.basic.service;

import io.github.jokoframework.myproject.basic.dto.CountryDTO;
import io.github.jokoframework.myproject.basic.entities.CountryEntity;

import java.util.List;

/**
 * Created by danicricco on 2/25/18.
 */
public interface CountryService {

    public List<CountryDTO> listCountries();

    CountryEntity save(CountryDTO countryDTO);

    void delete(String id);
}
