import re
import pandas as pd

class AttributeDistributions:
    def __group_dist(self, base, groups):
        mats = {}

        # Load matrices for each group
        for group in groups:
            f = '{}/group{}.txt'.format(base, group)
            dfs = self.__load_matrices(f)
            mats[group] = dfs

        # Keys are origin wards
        keys = range(1, 45)

        dist = {}

        # Combine different groups into distributions
        for k in keys:
            dfs = []

            # Get the cross-tabulation for each group
            for group in mats.keys():
                if k in mats[group]:
                    dfs.append(mats[group][k])
                else:
                    dfs.append(pd.DataFrame())

            # Create the distribution for this key
            dist[k] = self.__create_distribution(dfs)
            
        return dist
   
    def __load_matrices(self, f):
        # Load text file
        with open(f, 'r') as of:
            raw = of.readlines()
        L = list(map(str.strip, raw[22:]))
        N = len(L)

        dfs = {}
        for i in range(N):
            # New matrix
            if L[i][:5] == 'TABLE':
                # Get origin ward
                o = int(re.search(r"\d+", L[i]).group(0))

                # Skip empty line
                i += 2

                # Get header
                header = L[i].split()
                i += 1

                # Read matrix
                mat = []
                while i < N and L[i] != '':
                    mat.append(L[i].split())
                    i += 1
                df = pd.DataFrame(mat, columns=header).set_index('start_time')
                df = df.astype('int32')
                df = df.pivot(columns='ward_dest', values='total')
                dfs[o] = df
                
        return dfs

    def __create_distribution(self, dfs):
        # Take in matrices for each group, and combine into one distribution
        rows = set([i for m in dfs for i in m.index])
        cols = set([i for m in dfs for i in m.columns])
        N = len(dfs)

        # Impute missing columns
        for c in cols:
            for i in range(len(dfs)):
                if c not in dfs[i].columns:
                    dfs[i][c] = 0

        # Merge
        res = dfs[0]
        for i in range(1, N):
            res = res.merge(dfs[i],
                            how='outer',
                            left_index=True,
                            right_index=True,
                            suffixes=[None, "_" + str(i)])

        # Clean up
        res.index = res.index.astype(int)
        res.sort_index(inplace=True)
        res.fillna(0, inplace=True)

        # Merge column groups into lists
        for c in cols:
            col_group = [c] + ['{}_{}'.format(c, i) for i in range(1, N)]
            res[c] = res[col_group].values.tolist()

        return res.loc[:, cols]

    def get_distributions(self, path, groups):
        return self.__group_dist(path, groups)

if __name__ == "__main__":
    age_path = 'tts/age'
    age_groups = range(1, 9)
    income_path = 'tts/income'
    income_groups = range(1, 8)

    dists = AttributeDistributions()
    
    print("Getting age distributions")
    age = dists.get_distributions(age_path, age_groups)
    print(age[1].head())

    print("Getting income distributions")
    income = dists.get_distributions(income_path, income_groups)
    print(income[1].head())