package com.unam.repository;

import com.unam.model.Veterinario;

public class VeterinarioRepository extends RepositorioBase<Veterinario, Long> {
    public VeterinarioRepository() { super(Veterinario.class); }
}
