import React from 'react';
import clsx from 'clsx';
import styles from './HomepageFeatures.module.css';

const FeatureList = [
  {
    title: 'Focus on inventories',
    description: (
      <>
        ECInventory is focused on inventories customization, so it doesn't contain
        inventory-unrelated features like pets, custom items and etc.
      </>
    ),
  },
  {
    title: 'Highly customizable',
    description: (
      <>
        ECInventory designed with the idea to provide as flexible as possible
        configs. HOCON format allows to use inheritance, substitution and so on.
      </>
    ),
  },
  {
    title: 'Open Source',
    description: (
      <>
        You can participate in development, track project roadmap, discuss features
        and vote for favorite ones on GitHub.
      </>
    ),
  },
];

function Feature({title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
