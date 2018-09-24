

import pandas as pd
import numpy as np
from itertools import product
from sklearn.model_selection import KFold
from functools import reduce
from tqdm import tqdm


class MeanEncoder():

    def __init__(self, encoding_type='kfold', kfolds=10, smooth_k=30):
        assert encoding_type in {'kfold', 'loo', 'smooth',
                                 'expand'}, "encoding_type must in {'kfold','loo','smooth','expand'}"
        self.data = None
        self.mappings = dict()
        self.columns = None  # колонки кодирование средним которых необходимо произвести
        self.kfolds = kfolds
        self.smooth_k = smooth_k
        self.fillna_value = None

        self.encoding_type = encoding_type
        self.method = {'kfold': self._kfold_transform,
                       'loo': self._loo_transform,
                       'smooth': self._smoothing_transform,
                       'expand': self._expanding_mean_transform}

    def _create_mapping(self):
        for column in self.columns:
            self.mappings[column] = self.data.groupby(column).target.mean()

    def _kfold_transform(self, column):
        kf = KFold(n_splits=self.kfolds, shuffle=False)
        result = pd.Series()
        for train_index, transfrom_index in kf.split(self.data):
            chunk = self.data[column].iloc[transfrom_index].map(
                self.data[[column] + ['target']].iloc[train_index].groupby(column).target.mean())
            result = result.append(chunk)
        self.data[column + '_kfold_mean'] = result

    def _loo_transform(self, column):
        counts_dict = self.data[column].value_counts()
        sums_dict = self.data.groupby(column).target.sum()
        counts = self.data[column].map(counts_dict)
        sums = self.data[column].map(sums_dict)
        self.data[column + '_loo_mean'] = (sums - self.data.target) / (counts - 1)

    def _smoothing_transform(self, column):
        counts_dict = self.data[column].value_counts()
        counts = self.data[column].map(counts_dict)
        means_dict = self.data.groupby(column).target.mean()
        means = self.data[column].map(means_dict)
        self.data[column + '_smooth_mean'] = (np.multiply(counts, means) + self.fillna_value * self.smooth_k) / \
                                             (counts + self.smooth_k)

    def _expanding_mean_transform(self, column):
        cumsum = self.data.groupby(column)['target'].cumsum() - self.data['target']
        cumcnt = self.data.groupby(column).cumcount()
        self.data[column + '_expand_mean'] = cumsum / cumcnt

    def _exists(self, columns):
        if set(columns) - set(self.data.columns):
            raise ('%s нет в наборе данных.' % str(set(columns) - set(self.data.columns)))

    def _get_interactions(self, data):
        # создаем новые переменные производные от наборов содержащихся в interact_cols
        print('Генерация признаков совместной встречаемости...')
        add_columns = list()
        for column_set in tqdm(self.interact_cols):
            new_column = 'inter_' + '_'.join(column_set)
            data[new_column] = reduce(lambda x, y: x + '_' + y, [data[column].astype(str) for column in column_set])
            data[new_column] = data[new_column].astype('category')
            add_columns.append(new_column)  # добавляем сгенерированную кононку в список колнок одлежищих транформации
        return add_columns

    def train_generator(self, data, target, columns):
        self.data = data.copy()
        self.data['target'] = target.astype(np.float16)
        self.fillna_value = target.mean()  # !!!!!!!!!!!!!!!!!!!!! Подумать над этим.

        # проверка корректности списка columns
        map(self._exists, columns)

        # single_cols - список содержащий одночные колонки для кодирования средним.
        self.single_cols = list(filter(lambda x: (type(x) == str), columns))
        self.columns = self.single_cols.copy()

        # interact_cols - список содержащий наборы колонок для производства признаков
        # основанных на декартовом произведении значений колонок из наборов.
        self.interact_cols = list(filter(lambda x: type(x) in {list, tuple}, columns))

        self.columns += self._get_interactions(self.data)

        print('Кодирование средним. Схема %s...' % self.encoding_type)
        # вызов соотвествующего метода трансформации:
        for column in tqdm(self.columns):
            self.method[self.encoding_type](column)

        # для преобразования тестовой выборки
        self._create_mapping()

        # оставить только новые признаки
        drop_columns = set(self.data.columns) - {column + '_%s_mean' % self.encoding_type for column in self.columns}
        self.data.drop(columns=drop_columns, inplace=True)

        # for column in list(filter(lambda x: not x.endswith('_mean'), self.data.columns)):
        # del self.data[column]

        self.data.fillna(self.fillna_value, inplace=True)

        # чтобы внутри объекта не было ссылки на dataframe
        link = self.data
        self.data = None
        return link

    def test_generator(self, data):
        data = data.copy()
        self._get_interactions(data)
        # вызов соотвествующего метода трансформации:
        print('Кодирование средним...')
        for column in tqdm(self.columns):
            new_column = column + '_%s_mean' % self.encoding_type
            data[new_column] = data[column].map(self.mappings[column])

        drop_columns = set(data.columns) - {column + '_%s_mean' % self.encoding_type for column in self.columns}
        data.drop(columns=drop_columns, inplace=True)
        return data