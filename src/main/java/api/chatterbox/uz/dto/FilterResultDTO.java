package api.chatterbox.uz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterResultDTO<T> {
    private List<T> list;
    private Long totalCount;

    public FilterResultDTO(List<T> list, Long totalCount) {
        this.list = list;
        this.totalCount = totalCount;
    }
}
