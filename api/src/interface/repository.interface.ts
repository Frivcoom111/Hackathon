export interface Paginated<T> {
  data: T[];
  total: number;
}

export interface IRepository<T, createDTO, updateDTO> {
  findAll(skip: number, take: number): Promise<Paginated<T>>;
  findById(id: string): Promise<T | null>;
  create(data: createDTO): Promise<T>;
  update(id: string, data: updateDTO): Promise<T | null>;
  delete(id: string): Promise<void>;
}
