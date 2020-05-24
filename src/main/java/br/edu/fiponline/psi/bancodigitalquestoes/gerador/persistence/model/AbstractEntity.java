package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @Column(columnDefinition = "padrão booleano true", nullable = false)
    private boolean enabled = true;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
