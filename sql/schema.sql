Table usuario {
  id_usuario bigint [pk, increment]
  nome varchar(100)
  email varchar(120) [unique]
  senha_hash varchar(255)
  ativo boolean
  perfil varchar(30)
}

Table conta {
  id_conta bigint [pk, increment]
  nome_conta varchar(120)
  segmento varchar(80)
  produto_principal varchar(100)
  porte_empresa varchar(50)
  ano_inicio_cliente int
  status_conta varchar(20)
  cidade varchar(100)
  estado char(2)
  regiao varchar(40)
}

Table campanha {
  id_campanha bigint [pk, increment]
  id_conta bigint [ref: > conta.id_conta]
  nome_campanha varchar(150)
  canal varchar(60)
  objetivo varchar(80)
  segmento varchar(80)
  produto varchar(100)
  status_campanha varchar(20)
  posicao_funil varchar(50)
  abrangencia_geografica varchar(60)
  data_inicio date
  data_fim date
  orcamento_planejado decimal(15,2)
}

Table anuncio {
  id_anuncio bigint [pk, increment]
  id_campanha bigint [ref: > campanha.id_campanha]
  nome_anuncio varchar(150)
  formato varchar(60)
  versao_teste_ab char(1)
  linha_criativa varchar(80)
  status_anuncio varchar(20)
  nota_qualidade_criativo decimal(4,2)
}

Table publico_alvo {
  id_publico bigint [pk, increment]
  nome_publico varchar(120)
  faixa_etaria varchar(20)
  genero varchar(30)
  tipo_segmentacao varchar(80)
  intencao_compra varchar(30)
  tamanho_estimado_publico bigint
  cidade varchar(100)
  estado char(2)
  regiao varchar(40)
}

Table landing_page {
  id_landing_page bigint [pk, increment]
  id_campanha bigint [ref: > campanha.id_campanha]
  nome_landing_page varchar(150)
  tipo_pagina varchar(80)
  produto varchar(100)
  visitas bigint
  conversoes bigint
  tempo_medio_pagina_min decimal(6,2)
  status_landing_page varchar(20)
}

Table email_marketing {
  id_email bigint [pk, increment]
  id_conta bigint [ref: > conta.id_conta]
  tipo_email varchar(80)
  envios bigint
  aberturas bigint
  cliques bigint
  conversoes bigint
  descadastros bigint
  data date
  custo_envio decimal(15,2)
  receita_atribuida decimal(15,2)
}

Table resumo_funil {
  id_funil bigint [pk, increment]
  id_campanha bigint [ref: > campanha.id_campanha]
  data date
  impressoes bigint
  cliques bigint
  leads bigint
  oportunidades bigint
  vendas bigint
  custo decimal(15,2)
  receita decimal(15,2)
}

Table desempenho_diario {
  id_desempenho bigint [pk, increment]
  id_anuncio bigint [ref: > anuncio.id_anuncio]
  id_publico bigint [ref: > publico_alvo.id_publico]
  dispositivo varchar(40)
  data date
  impressoes bigint
  cliques bigint
  leads bigint
  oportunidades bigint
  vendas bigint
  custo decimal(15,2)
  receita decimal(15,2)
}