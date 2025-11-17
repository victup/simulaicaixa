package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.TelemetriaRegistroEntity;

import java.time.OffsetDateTime;
import java.util.List;

public interface TelemetriaRegistroRepository {

    void salvar(TelemetriaRegistroEntity registro);

    List<TelemetriaRegistroEntity> listarPorPeriodo(OffsetDateTime inicio, OffsetDateTime fim);
}