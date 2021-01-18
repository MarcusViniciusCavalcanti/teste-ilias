export class Transactional {
  id: number;
  value: number;
  transactionalType: string;
  idAllottee: number;
  date: string;
  status: string;
  
}

export enum Type {
  INCREMENT, DECREMENT
}
