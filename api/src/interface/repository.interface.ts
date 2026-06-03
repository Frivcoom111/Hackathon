export interface IRepository<T, createDTO, updateDTO> {
  findAll(): Promise<T[]>;
  findById(id: string): Promise<T | null>;
  create(data: createDTO): Promise<T | null>;
  update(id: string, data: updateDTO): Promise<T | null>;
  delete(id: string): Promise<void>;
}
