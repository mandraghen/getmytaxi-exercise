package org.smorabito.getmytaxy.load.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"price"})
public class Checkpoint extends Wall {
    private int price;
}
