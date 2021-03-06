import { Transactional } from './transactional';

export class PageTransactional {
  content: Transactional[];
  pageable: Pegeable;
  totalElements: number;
  totalPages: number;
  last: boolean;
  number: number;
  sort: Sort;
  size: number;
  firts: boolean;
  numberOfElements: number;
  empty: boolean;
}

export class Pegeable {
  sort: Sort;
  offset: number;
  pageNumber: number;
  pageSize: number;
  paged: boolean;
  unpaged: boolean;
}

export class Sort {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
}
