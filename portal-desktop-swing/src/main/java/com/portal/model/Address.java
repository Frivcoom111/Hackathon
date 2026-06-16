package com.portal.model;

/**
 * Classe Address (Endereço): representa um endereço físico.
 *
 * É usada por outras entidades (User, Student, Company) que precisam guardar
 * onde a pessoa ou empresa está localizada. Funciona como um "bloco" reutilizável.
 */
public class Address {
    // ===== ATRIBUTOS do endereço =====
    private String id;          // Identificador único do endereço (chave primária).
    private String street;      // Logradouro (nome da rua/avenida).
    private String number;      // Número do imóvel.
    private String complement;  // Complemento (apartamento, bloco, etc.).
    private String district;    // Bairro.
    private String city;        // Cidade.
    private String state;       // Estado (UF).
    private String zipCode;     // CEP (armazenado apenas com números, sem traço).

    /** Construtor vazio: cria um endereço "em branco". */
    public Address() {}

    /** Construtor completo: cria um endereço já preenchido. */
    public Address(String id, String street, String number, String complement,
                   String district, String city, String state, String zipCode) {
        this.id         = id;
        this.street     = street;
        this.number     = number;
        this.complement = complement;
        this.district   = district;
        this.city       = city;
        this.state      = state;
        this.zipCode    = zipCode;
    }

    // ===== GETTERS: leem os valores =====
    public String getId()          { return id; }
    public String getStreet()      { return street; }
    public String getNumber()      { return number; }
    public String getComplement()  { return complement; }
    public String getDistrict()    { return district; }
    public String getCity()        { return city; }
    public String getState()       { return state; }
    public String getZipCode()     { return zipCode; }

    // ===== SETTERS: definem/alteram os valores =====
    public void setId(String id)                  { this.id = id; }
    public void setStreet(String street)          { this.street = street; }
    public void setNumber(String number)          { this.number = number; }
    public void setComplement(String complement)  { this.complement = complement; }
    public void setDistrict(String district)      { this.district = district; }
    public void setCity(String city)              { this.city = city; }
    public void setState(String state)            { this.state = state; }
    public void setZipCode(String zipCode)        { this.zipCode = zipCode; }

    /**
     * Formata o CEP para exibição no padrão brasileiro "00000-000".
     *
     * Internamente o CEP é guardado só com números (ex.: "01001000"). Este método
     * adiciona o traço para ficar mais legível na tela.
     *
     * @return o CEP formatado (ex.: "01001-000"), ou o valor original caso ele
     *         não tenha exatamente 8 dígitos (proteção contra dados inválidos).
     */
    public String formatarCep() {
        // Se o CEP for nulo ou não tiver 8 dígitos, devolve como está (não dá pra formatar).
        if (zipCode == null || zipCode.length() != 8) return zipCode;
        // Pega os 5 primeiros dígitos + "-" + os 3 últimos. Ex.: "01001" + "-" + "000".
        return zipCode.substring(0, 5) + "-" + zipCode.substring(5);
    }
}
